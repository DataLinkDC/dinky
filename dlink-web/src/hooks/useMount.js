import { useEffect } from 'react';

/**
 * @hook useMount
 * @desc ComponentDidMount likes
 * */
const useMount = callback => {
  useEffect(callback, []);
};

export default useMount;
