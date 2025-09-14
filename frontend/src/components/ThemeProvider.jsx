import { useMemo } from "react";
import { ThemeProvider as MuiThemeProvider } from "@mui/material";
import { ThemeContext, useSystemColorMode } from "../hooks/useTheme";
import getTheme from "../util/theme";


//Create the component that holds the state
export const ThemeProvider = ({ children }) => {
  const [mode, toggleMode] = useSystemColorMode();

  //get the mui theme object based on the mode
  //useMemo to prevent re-creating the theme object on every render
  const muiTheme = useMemo(() => {
    return getTheme(mode);
  }, [mode]);

  const value = {
    mode,
    toggleMode
  }

  //react useContext() returns the current context value, as given
  //by the nearest context provider, here is it the value object above
  return (
    <ThemeContext.Provider value={value}>
      <MuiThemeProvider theme={muiTheme}>
        { children }
      </MuiThemeProvider>
    </ThemeContext.Provider>
  );
  
}