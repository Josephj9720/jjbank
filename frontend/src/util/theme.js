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
      paper: "#EDF3FC",
    },
    navbar: {
      background: "#0C2850",
      text: "#EDF3FC",
    },
    authButton: {
      outlined: {
        background: "#00000000",
        text: "#054480",
        ripple: "#054480",
      },
      contained: {
        background: "#054480",
        text: "#EDF3FC",
        ripple: "#EDF3FC",
      }
    }
  },
  assets: {
    navbar: {
      appIcon: "src/assets/jjbank-logo-light.svg",
      appIconAria: "JJBank Logo Light",
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
      paper: "#0B1215",
    },
    navbar: {
      background: "#0B1215",
      text: "#F3F3F3",
    },
    authButton: {
      outlined: {
        background: "#00000000",
        text: "#F3F3F3",
        ripple: "#F3F3F3",
      },
      contained: {
        background: "#0C2850",
        text: "#F3F3F3",
        ripple: "#F3F3F3",
      }
    }
  },
  assets: {
    navbar: {
      appIcon: "src/assets/jjbank-logo-dark.svg",
      appIconAria: "JJBank Logo Dark",
    }
  }
})


export default getTheme;