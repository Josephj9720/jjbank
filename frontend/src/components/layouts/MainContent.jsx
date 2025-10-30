import { Box } from "@mui/material";

const MainContent = ({ children }) => {
  return (
    <Box
      component="main"
      sx={{
        "display" : "flex",
        "flexDirection" : "column",
        "minHeight" : "100%",
        "margin" : "0",
      }}
    >
      <Box 
        className="toolbarOffset"
        sx={ (theme) => theme.mixins.toolbar }
      ></Box>
      {children}
    </Box>
  );
}

export default MainContent;