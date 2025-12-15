import { Box } from "@mui/material";

const SectionRow = ({ children, component }) => {

  return (
    <Box
      component={component}
      sx={{
        "display": "flex",
        "flexDirection": "row",
        "width": "100%",
      }}
    >
      {children}
    </Box>
  );

};

export default SectionRow;