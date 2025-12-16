import { Box } from "@mui/material";

const SectionRow = ({ 
  children, 
  component, 
  componentProps, 
  textDecoration = "none" }) => {

  return (
    <Box
      component={component}
      {...componentProps}
      sx={{
        "display": "flex",
        "flexDirection": "row",
        "width": "100%",
        "textDecoration": textDecoration,
      }}
    >
      {children}
    </Box>
  );

};

export default SectionRow;