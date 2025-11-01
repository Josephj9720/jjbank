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
    },
    welcomeBanner: {
      background: "#EDF3FC",
      text: "#054480",
      border: "#054480",
    },
    section: {
      background: "#EDF3FC",
      border: "#054480",
      primaryText: "#051223",
      secondaryText: "#054480",
    }
  },
  assets: {
    navbar: {
      appIcon: "src/assets/jjbank-logo-light.svg",
      appIconAria: "JJBank Logo Light",
    }
  },
  layout: {
    welcomeBanner: {
      borderWidth: "5px",
    },
    section: {
      alignment: {
        left: "0% 28% 2% 16%",
        right: "0% 16% 2% 28%",
        center: "2% 22% 0% 22%",
      },
      borderWidth: "5px",
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
    },
    welcomeBanner: {
      background: "#0C2850",
      text: "#F3F3F3",
      border: "#0C2850",
    },
    section: {
      background: "#0C2850",
      border: "#0C2850",
      primaryText: "#F3F3F3",
      secondaryText: "#EDF3FC",
    }
  },
  assets: {
    navbar: {
      appIcon: "src/assets/jjbank-logo-dark.svg",
      appIconAria: "JJBank Logo Dark",
    }
  },
  layout: {
    welcomeBanner: {
      borderWidth: "0px",
    },
    section: {
      alignment: {
        left: "0% 28% 2% 16%",
        right: "0% 16% 2% 28%",
        center: "2% 22% 0% 22%",
      },
      borderWidth: "0px",
    }
  }
});


export default getTheme;