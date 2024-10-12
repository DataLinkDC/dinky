import {STUDIO_MODEL} from "@/pages/DataStudioNew/model";
import {Dispatch} from "@umijs/max";
import {
  CenterTabDTO,
  HandleLayoutChangeDTO,
  InitSaveLayoutDTO,
  PayloadType, ProjectDTO,
  SaveToolbarLayoutDTO, UpdateActionDTO
} from "@/pages/DataStudioNew/type";

export const mapDispatchToProps = (dispatch: Dispatch) => {
  return {
    initSaveLayout: (payload: InitSaveLayoutDTO) =>{
      dispatch({
        ...payload,
        type: STUDIO_MODEL.initSaveLayout
      })
    },
    handleLayoutChange: (payload: HandleLayoutChangeDTO) =>
      dispatch({
        ...payload,
        type: STUDIO_MODEL.handleLayoutChange,
      }),
    handleToolbarShowDesc: () =>
      dispatch({
        type: STUDIO_MODEL.handleToolbarShowDesc
      }),
    saveToolbarLayout: (payload: SaveToolbarLayoutDTO) =>
      dispatch({
        ...payload,
        type: STUDIO_MODEL.saveToolbarLayout
      }),
    addCenterTab: (payload: CenterTabDTO) =>
      dispatch({
        ...payload,
        type: STUDIO_MODEL.addCenterTab
      }),
    updateProject: (payload: ProjectDTO) =>
      dispatch({
        ...payload,
        type: STUDIO_MODEL.updateProject
      }),
    updateAction: (payload:UpdateActionDTO) =>
      dispatch({
        ...payload,
        type: STUDIO_MODEL.updateAction
      }),

  }
}
