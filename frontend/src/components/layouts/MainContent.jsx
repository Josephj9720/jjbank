import { Box } from "@mui/material";

const MainContent = ({ children }) => {
  return (
    <Box
      component="main"
      sx={{
        "display" : "flex",
        "minHeight" : "100%",
        "margin" : "0",
        "paddingTop" : (theme) => `${theme.mixins.toolbar.minHeight}px`,
        "border" : "2px solid blue"
      }}
    >
      {children}
    </Box>
  );
}

export default MainContent;