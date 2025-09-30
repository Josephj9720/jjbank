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
      default: "#EDF3FC",
    },
    navbar: {
      background: "#0C2850",
      text: "#EDF3FC",
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
    },
    background: {
      default: "#0B1215",
    },
    navbar: {
      background: "#0B1215",
      text: "#F3F3F3",
    }
  }
})


export default getTheme;