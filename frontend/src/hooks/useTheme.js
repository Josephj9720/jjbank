import { createContext, useContext, useState, useEffect } from "react";

//create a Context object with default value
export const ThemeContext = createContext();

//custom hook to use context easily
//it returns the value prop given to the nearest context provider
export const useTheme = () => useContext(ThemeContext);

//custom hook to return the state and toggle method for the theme's mode
//based on system or local/app preference
export function useSystemColorMode() {

  const [appMode, setAppMode] = useState("light");
  
  //check LocalStorage and system preference on first load
  useEffect(() => {
    const localMode = window.localStorage.getItem("mode");

    //returns true if match media query list
    const systemPrefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches; 

    if(localMode) {
      setAppMode(localMode);
    } else if(systemPrefersDark) {
      setAppMode("dark");
    }

  }, []);

  //update mode and LocalStorage when user toggles the button
  const toggleAppMode = () => {
    setAppMode(prevMode => {
      const newMode = prevMode === "light" ? "dark" : "light";
      window.localStorage.setItem("mode", newMode);
      return newMode;
    });
  }

  //listen for system changes iff the user hasn't set 
  //their preference using the button
  useEffect(() => {
    const mediaQuery = window.matchMedia("prefers-color-scheme: dark");
    const handleSystemChange = () => {
      //check if preference already exists in local storage
      if(!window.localStorage.getItem("mode")) {
        setAppMode(mediaQuery.matches ? 'dark' : 'light');
      }
    };

    mediaQuery.addEventListener("change", handleSystemChange);
    //remove event listener on unmount: prevents memory leaks, unintended erros, etc.
    return () => {
      mediaQuery.removeEventListener("change", handleSystemChange);
    };
  }, []);

  return [appMode, toggleAppMode];
};