import React from "react";

export function useLocalStorage(key: string, initialValue: any) {

  const readValue = React.useCallback(() => {
    try {
      const item = window.localStorage.getItem(key);
      return item ? JSON.parse(item) : initialValue;
    } catch (error) {
      console.warn(error);
      return initialValue;
    }
  }, [key, initialValue]);

  const [localState, setLocalState] = React.useState(readValue);

  const handleSetState = React.useCallback(
    (value: any) => {
      try {
        const nextState =
          typeof value === "function" ? value(localState) : value;
        window.localStorage.setItem(key, JSON.stringify(nextState));
        setLocalState(nextState);
        window.dispatchEvent(new Event("local-storage"));
      } catch (e) {
        console.warn(e);
      }
    },
    [key, localState]
  );

  return [localState, handleSetState];
}
