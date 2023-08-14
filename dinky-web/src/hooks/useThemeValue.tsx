import {THEME} from "@/types/Public/data";
import {useEffect, useState} from "react";
import {theme} from "antd";

export type ThemeValue = {
  borderColor: string
  footerColor: string
}
const { useToken } = theme;
const getThemeValue = (isDark: boolean): ThemeValue => {
  if (isDark) {
    return {borderColor: "#343434", footerColor: "rgba(255, 255, 255, 0.18)"}
  } else {
    return {borderColor: "rgba(5, 5, 5, 0.06)", footerColor: "#f4f4f4"}
    // return {borderColor: "#E0E2E5"}
  }
}
export default function useThemeValue() {
  const {token} = useToken();
  // const {initialState} = useModel("@@initialState");
  const [theme,setTheme] = useState(localStorage.getItem(THEME.NAV_THEME))

  const isDark = theme === THEME.dark;
  useEffect(() => {
    setTheme(localStorage.getItem(THEME.NAV_THEME))
  },[localStorage.getItem(THEME.NAV_THEME)]);
  return getThemeValue(isDark);
};

