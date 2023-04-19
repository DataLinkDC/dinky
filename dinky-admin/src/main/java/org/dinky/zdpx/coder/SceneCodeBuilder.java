package org.dinky.zdpx.coder;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.dinky.zdpx.coder.code.CodeBuilder;
import org.dinky.zdpx.coder.code.CodeJavaBuilderImpl;
import org.dinky.zdpx.coder.code.CodeSqlBuilderImpl;
import org.dinky.zdpx.coder.graph.OperatorWrapper;
import org.dinky.zdpx.coder.graph.Scene;
import org.dinky.zdpx.coder.json.ResultType;
import org.dinky.zdpx.coder.json.SceneNode;
import org.dinky.zdpx.coder.operator.Identifier;
import org.dinky.zdpx.coder.operator.Operator;
import org.dinky.zdpx.coder.utils.InstantiationUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 配置场景生成操作类
 *
 * @author Licho Sun
 */
@Slf4j
public class SceneCodeBuilder {
    // 自定义函数操作算子集
    private Map<String, String> udfFunctionMap = new HashMap<>();

    private CodeBuilder codeBuilder;
    private final Scene scene;

    public SceneCodeBuilder(Scene scene) {
        this.scene = scene;
        if (scene.getEnvironment().getResultType() == ResultType.JAVA) {
            CodeContext codeContext = createCodeContext(scene);
            codeBuilder = new CodeJavaBuilderImpl(codeContext);
        } else if (scene.getEnvironment().getResultType() == ResultType.SQL) {
            codeBuilder = new CodeSqlBuilderImpl();
        }
    }

    public CodeBuilder getGenerateResult() {
        return codeBuilder;
    }

    public void setGenerateResult(CodeBuilder codeBuilder) {
        this.codeBuilder = codeBuilder;
    }

    public Scene getScene() {
        return scene;
    }

    public Map<String, String> getUdfFunctionMap() {
        return udfFunctionMap;
    }

    public void setUdfFunctionMap(Map<String, String> udfFunctionMap) {
        this.udfFunctionMap = udfFunctionMap;
    }

    /**
     * 根据场景配置生成可执行的java源文件
     *
     * @throws IOException ignore
     */
    public String build() {
        if (codeBuilder == null) {
            throw new IllegalStateException(String.format("Code Builder %s empty!", codeBuilder));
        }

        codeBuilder.firstBuild();
        createOperatorsCode();
        return codeBuilder.lastBuild();
    }

    /**
     * 广度优先遍历计算节点, 生成相对应的源码
     */
    private void createOperatorsCode() {
        List<OperatorWrapper> sinkOperatorNodes = Scene.getSinkOperatorNodes(this.scene.getProcess());
        List sinks =
            sinkOperatorNodes.stream().map(OperatorWrapper::getOperator).collect(Collectors.toList());
        Deque<Operator> ops = new ArrayDeque<>();
        bft(Set.copyOf(sinks), ops::push);
        ops.stream().distinct().forEach(this::operate);
    }

    /**
     * 广度优先遍历计算节点, 执行call 函数
     *
     * @param operators 起始节点集
     * @param call      待执行函数
     */
    private void bft(Set<Operator> operators, Consumer<Operator> call) {
        if (operators.isEmpty()) {
            return;
        }

        List<Operator> ops = operators.stream()
                .sorted(Comparator.comparing(t -> t.getOperatorWrapper().getId(), Comparator.naturalOrder()))
                .collect(Collectors.toList());
        final Set preOperators = new HashSet<Operator>();
        for (Operator op : ops) {
            call.accept(op);
            op.getInputPorts().stream()
                .filter(t -> !Objects.isNull(t.getConnection()))
                .map(t -> t.getConnection().getFromPort())
                .forEach(fromPort -> preOperators.add(fromPort.getParent()));
        }
        bft(preOperators, call);
    }

    /**
     * 执行每个宏算子的代码生成逻辑
     *
     * @param op 宏算子
     */
    private void operate(Operator op) {
        op.setSchemaUtil(this);
        op.run();
    }

    /**
     * 获取operator的定义, key为{@link Identifier#getCode()} 返回值, 目前为Operator类的全限定名
     * value为类型定义.
     *
     * @return 返回operator集
     */
    public static Map<String, Class<? extends Operator>> getCodeClassMap(
        Set<Class<? extends Operator>> operators) {
        return operators.stream()
            .filter(c -> !java.lang.reflect.Modifier.isAbstract(c.getModifiers()))
            .collect(Collectors.toMap(t -> {
                Operator bcg = InstantiationUtil.instantiate(t);
                String code = bcg.getCode();
                bcg = null;
                return code;
            }, t -> t));
    }

    /**
     * 创建场景代码上下文
     *
     * @param scene 场景类
     * @return 代码上下文
     */
    public CodeContext createCodeContext(Scene scene) {
        return CodeContext.newBuilder(scene.getEnvironment().getName()).scene(scene).build();
    }

    /**
     * 读取配置文件, 生成场景节点(对应于配置)
     *
     * @param filePath 配置文件路径
     * @return 场景节点
     */
    public static SceneNode readSceneFromFile(String filePath) {
        FileInputStream fis;
        try {
            fis = new FileInputStream(filePath);
            return readScene(fis);
        } catch (FileNotFoundException e) {
            log.error("readScene error, file not exists {}", e.getMessage());
        }
        return null;
    }

    /**
     * 根据输入流, 生成场景节点(对应于配置)
     *
     * @param in 配置文件输入流
     * @return 场景节点
     */
    public static SceneNode readScene(InputStream in) {
        return readSceneInternal(in);
    }

    /**
     * 根据输入流, 生成场景节点(对应于配置)
     *
     * @param in 配置文件输入流
     * @return 场景节点
     */
    public static SceneNode readScene(String in) {
        return readSceneInternal(in);
    }

    private static SceneNode readSceneInternal(Object in) {
        final ObjectMapper objectMapper = new ObjectMapper();
        SceneNode scene;
        try {
            if (in instanceof String) {
                scene = objectMapper.readValue((String) in, SceneNode.class);
            } else if (in instanceof InputStream) {
                scene = objectMapper.readValue((InputStream) in, SceneNode.class);
            } else {
                return null;
            }

            scene.initialize();
            return scene;
        } catch (IOException e) {
            log.error("readScene error, exception {}", e.getMessage());
        }
        return null;
    }

    /**
     * 将外部场景节点(对应于配置文件)转换为内部场景类
     *
     * @param sceneNode 外部场景节点
     * @return 内部场景类
     */
    public static Scene convertToInternal(SceneNode sceneNode) {
        return new Scene(sceneNode);
    }


}
