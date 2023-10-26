import {
  createContext,
  Dispatch,
  FC,
  memo,
  SetStateAction,
  useContext,
  useEffect,
  useState
} from 'react';
import { flushSync } from 'react-dom';
import { useModel } from 'umi';
export interface FullScreenContextProps {
  fullscreen: boolean;
  setFullscreen: Dispatch<SetStateAction<boolean>>;
}

export const FullScreenContext = createContext<FullScreenContextProps>(
  {} as FullScreenContextProps
);

export const FullScreenProvider: FC = memo(({ children }) => {
  const [fullscreen, setFullscreen] = useState<boolean>(false);

  const { initialState, setInitialState } = useModel('@@initialState');

  useEffect(() => {
    flushSync(() => {
      setInitialState((s) => ({
        ...s,
        fullscreen
      }));
    });
  }, [fullscreen]);
  return (
    <FullScreenContext.Provider value={{ fullscreen, setFullscreen }}>
      {children}
    </FullScreenContext.Provider>
  );
});

export const useEditor = () => useContext(FullScreenContext);
