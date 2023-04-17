import LogicFlow from "@logicflow/core";


class FlinkGraph {
    static pluginName = 'FlinkGraph';

    constructor(public lf: LogicFlow) {
        lf.adapterIn = this.adapterIn;

    }

    adapterIn = (trees) => {
        const tree = {
            children: trees
        }

        const newTree = this.convertToTree(tree);
    };

    private convertToTree(tree: { children: any }): any {
        if (!tree || !tree.children || tree.children.length === 0) {
            return tree;
        }


    }
}
