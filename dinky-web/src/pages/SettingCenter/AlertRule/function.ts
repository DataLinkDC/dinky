export const validationOptions = (value: string) => {
  switch (value) {
    case 'jobInstance.status':
    case 'checkPoints.checkFailed(#key,#checkPoints)':
    case 'exceptionRule.isException(#key,#exceptions)':
      return true;
    case 'checkPoints.checkpointTime(#key,#checkPoints)':
      return false;
    default:
      return false;
  }
};
