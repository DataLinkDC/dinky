package com.dlink.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import com.dlink.common.result.ProTableResult;
import com.dlink.common.result.Result;
import com.dlink.dto.CatalogueTaskDTO;
import com.dlink.model.Catalogue;
import com.dlink.service.CatalogueService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * CatalogueController
 *
 * @author wenmo
 * @since 2021/5/28 14:03
 **/
@Slf4j
@RestController
@RequestMapping("/api/catalogue")
public class CatalogueController {
    @Autowired
    private CatalogueService catalogueService;

    @PostMapping("/upload/{id}")
    public Result<String> upload(MultipartFile file,@PathVariable Integer id) {
        //获取上传的路径
        String filePath = System.getProperty("user.dir");
        //获取源文件的名称
        String fileName = file.getOriginalFilename();
        String zipPath = filePath+File.separator+fileName;
        String unzipFileName = fileName.substring(0,fileName.lastIndexOf("."));
        String unzipPath = filePath+File.separator+unzipFileName;
        File unzipFile = new File(unzipPath);
        File zipFile = new File(zipPath);
        if(unzipFile.exists()){
            FileUtil.del(zipFile);
            return Result.failed("工程已存在");
        }
        try {
            //文件写入上传的路径
            FileUtil.writeBytes(file.getBytes(), zipPath);
            Thread.sleep(1L);
            if(!unzipFile.exists()){
                ZipUtil.unzip(zipPath,filePath);
                Catalogue cata = getCatalogue(id, unzipFileName);
                traverseFile(unzipPath,cata);
            }
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }finally {
            FileUtil.del(zipFile);
        }
        return Result.succeed("上传zip包并创建工程成功");
    }


    private void traverseFile(String sourcePath, Catalogue catalog) throws Exception{
        File file = new File(sourcePath);
        File[] fs = file.listFiles();
        if(fs == null){
            throw new RuntimeException("目录层级有误");
        }
        for (File fl : fs) {
            if(fl.isFile()){
                System.out.println(fl.getName());
                CatalogueTaskDTO dto = getCatalogueTaskDTO(fl.getName(), catalogueService.findByParentIdAndName(catalog.getParentId(), catalog.getName()).getId());
                String fileText = getFileText(fl);
                catalogueService.createCatalogAndFileTask(dto,fileText);
            }else{
                Catalogue newCata = getCatalogue(catalogueService.findByParentIdAndName(catalog.getParentId(), catalog.getName()).getId(), fl.getName());
                traverseFile(fl.getPath(),newCata);
            }
        }
    }

    private String getFileText(File sourceFile){
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try{
            if(sourceFile.isFile() && sourceFile.exists()){
                InputStreamReader isr = new InputStreamReader(new FileInputStream(sourceFile));
                br = new BufferedReader(isr);
                String lineText = null;
                while ((lineText = br.readLine()) != null){
                    sb.append(lineText).append("\n");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(br != null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    private Catalogue getCatalogue(Integer parentId, String name){
        Catalogue subcata = new Catalogue();
        subcata.setTaskId(null);
        subcata.setName(name);
        subcata.setType("null");
        subcata.setParentId(parentId);
        subcata.setIsLeaf(false);
        catalogueService.saveOrUpdate(subcata);
        return subcata;
    }

    private CatalogueTaskDTO getCatalogueTaskDTO(String alias, Integer parentId){
        CatalogueTaskDTO catalogueTaskDTO = new CatalogueTaskDTO();
        catalogueTaskDTO.setName(UUID.randomUUID().toString().substring(0,6)+alias);
        catalogueTaskDTO.setAlias(alias);
        catalogueTaskDTO.setId(null);
        catalogueTaskDTO.setParentId(parentId);
        catalogueTaskDTO.setLeaf(true);
        //catalogueTaskDTO.setDialect("FlinkSql");
        return catalogueTaskDTO;
    }

    /**
     * 新增或者更新
     */
    @PutMapping
    public Result saveOrUpdate(@RequestBody Catalogue catalogue) throws Exception {
        if(catalogueService.saveOrUpdate(catalogue)){
            return Result.succeed("创建成功");
        }else {
            return Result.failed("创建失败");
        }
    }

    /**
     * 动态查询列表
     */
    @PostMapping
    public ProTableResult<Catalogue> listCatalogues(@RequestBody JsonNode para) {
        return catalogueService.selectForProTable(para);
    }

    /**
     * 批量删除
     */
    @DeleteMapping
    public Result deleteMul(@RequestBody JsonNode para) {
        if (para.size()>0){
            boolean isAdmin = false;
            List<Integer> error = new ArrayList<>();
            for (final JsonNode item : para){
                Integer id = item.asInt();
                if(!catalogueService.removeCatalogueAndTaskById(id)){
                    error.add(id);
                }
            }
            if(error.size()==0&&!isAdmin) {
                return Result.succeed("删除成功");
            }else {
                return Result.succeed("删除部分成功，但"+error.toString()+"删除失败，共"+error.size()+"次失败。");
            }
        }else{
            return Result.failed("请选择要删除的记录");
        }
    }

    /**
     * 获取指定ID的信息
     */
    @PostMapping("/getOneById")
    public Result getOneById(@RequestBody Catalogue catalogue) throws Exception {
        catalogue = catalogueService.getById(catalogue.getId());
        return Result.succeed(catalogue,"获取成功");
    }

    /**
     * 获取所有目录
     */
    @PostMapping("/getCatalogueTreeData")
    public Result getCatalogueTreeData() throws Exception {
        List<Catalogue> catalogues = catalogueService.getAllData();
        return Result.succeed(catalogues,"获取成功");
    }

    /**
     * 创建节点和作业
     */
    @PutMapping("/createTask")
    public Result createTask(@RequestBody CatalogueTaskDTO catalogueTaskDTO) throws Exception {
        Catalogue catalogue = catalogueService.createCatalogueAndTask(catalogueTaskDTO);
        if(catalogue.getId()!=null){
            return Result.succeed(catalogue,"创建成功");
        }else {
            return Result.failed("创建失败");
        }
    }

    /**
     * 创建节点和作业
     */
    @PutMapping("/toRename")
    public Result toRename(@RequestBody Catalogue catalogue) throws Exception {
        if(catalogueService.toRename(catalogue)){
            return Result.succeed("重命名成功");
        }else {
            return Result.failed("重命名失败");
        }
    }
}
