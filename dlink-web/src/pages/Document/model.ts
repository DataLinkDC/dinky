import {Effect, Reducer} from "umi";
import {DocumentTableListItem} from "@/pages/Document/data";

export type DocumentStateType = {
  fillDocuments:DocumentTableListItem[],
};

export type ModelType = {
  namespace: string;
  state: DocumentStateType;
  effects: {
  };
  reducers: {
    saveAllFillDocuments: Reducer<DocumentStateType>;
  };
};

const DocumentModel: ModelType = {
  namespace: 'Document',
  state: {
    fillDocuments:[],
  },

  effects: {

  },

  reducers: {
    saveAllFillDocuments(state, {payload}) {
      return {
        ...state,
        fillDocuments: payload,
      };
    },

  },
};

export default DocumentModel;
