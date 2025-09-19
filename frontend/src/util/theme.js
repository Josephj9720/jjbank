import { createTheme } from "@mui/material";

function getTheme(mode) {
  return mode === 'light' ? lightTheme : darkTheme;
}

const lightTheme = createTheme({
  palette: {
    mode: 'light',
    primary: {
      main: "#0C2850",
    },
    secondary:  {
      main: "#054480",
    },
    background: {
      
    }
  }
});

//redefine those colors eventually
const darkTheme = createTheme({
  palette: {
    mode: 'dark',
    primary: {
      main: "#F3F3F3",
    },
    secondary: {
      main: "#0C2850",
    }
  }
})


export default getTheme;