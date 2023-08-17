import {THEME} from "@/types/Public/data";
import {useEffect, useState} from "react";
import {theme} from "antd";

export type ThemeValue = {
  borderColor: string
  footerColor: string
}

const getThemeValue = (isDark: boolean): ThemeValue => {
  if (isDark) {
    return {borderColor: "#343434", footerColor: "rgba(255, 255, 255, 0.18)"}
  } else {
    return {borderColor: "rgba(5, 5, 5, 0.06)", footerColor: "#f4f4f4"}
  }
}

export default function useThemeValue() {
  const [theme,setTheme] = useState(localStorage.getItem(THEME.NAV_THEME))

  useEffect(() => {
    setTheme(localStorage.getItem(THEME.NAV_THEME))
  },[localStorage.getItem(THEME.NAV_THEME)]);

  return getThemeValue(theme === THEME.dark);
};

