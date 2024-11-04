import {createContext} from 'react';


type DataStudioContextType = {
  theme: 'realDark' | 'light'
}
export const DataStudioContext = createContext({
  theme: 'light',
} as DataStudioContextType);
