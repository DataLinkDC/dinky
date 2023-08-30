interface BaseModelType {
  ['reducers']: {};
  ['effects']: {};
  ['namespace']: string;
}
type MemberType<T extends BaseModelType, U> = U extends 'effects' ? T['effects'] : T['reducers'];

function getModelTypes<T extends BaseModelType>(
  target: T,
  type: 'effects' | 'reducers'
): { [K in keyof MemberType<T, typeof type>]: string } {
  const reducers = Object.keys(target[type]).map((obj: string) => [
    obj,
    `${target.namespace}/${obj}`
  ]);
  // @ts-ignore
  return Object.fromEntries(new Map<keyof (typeof target)[type], string>(reducers));
}

export const createModelTypes = <T extends BaseModelType>(
  target: T
): [{ [K in keyof T['reducers']]: string }, { [K in keyof T['effects']]: string }] => {
  return [getModelTypes(target, 'reducers'), getModelTypes(target, 'effects')];
};
