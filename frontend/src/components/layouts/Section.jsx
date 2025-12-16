import { Box } from "@mui/material";

const Section = ({ 
  children,
  component = "div",
  componentProps,
  textDecoration = "none",
  sectionColor = "white", 
  borderColor = "black", 
  borderWidth = "1px", 
  margin = "0%",
  padding = "0%", }) => {
  return (
    <Box
      component={component}
      {...componentProps}
      sx={{
        "display": "flex",
        "flexDirection": "column",
        "margin": {
          "xs": "0% 0% 2% 0%",
          "sm": margin,
        },
        "padding": padding,
        "border": `${borderWidth} solid ${borderColor}`,
        "backgroundColor": sectionColor,
        "textDecoration" : textDecoration,
      }}
    >
      {children}
    </Box>
  );
};

export default Section;