import {STUDIO_MODEL} from "@/pages/DataStudioNew/model";
import {Dispatch} from "@umijs/max";
import {
  CenterTabDTO,
  HandleLayoutChangeDTO,
  ProjectDTO,
  SaveToolbarLayoutDTO,
  SetLayoutDTO,
  UpdateActionDTO
} from "@/pages/DataStudioNew/type";

export const mapDispatchToProps = (dispatch: Dispatch) => {
  return {
    setLayout: (payload: SetLayoutDTO) => {
      dispatch({
        ...payload,
        type: STUDIO_MODEL.setLayout
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
    removeCenterTab: (id: string) =>
      dispatch({
        id,
        type: STUDIO_MODEL.removeCenterTab
      }),
    updateProject: (payload: ProjectDTO) =>
      dispatch({
        ...payload,
        type: STUDIO_MODEL.updateProject
      }),
    updateAction: (payload: UpdateActionDTO) =>
      dispatch({
        ...payload,
        type: STUDIO_MODEL.updateAction
      }),

  }
}
