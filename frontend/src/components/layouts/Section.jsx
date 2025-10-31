import { Box } from "@mui/material";

const Section = ({ 
  children, 
  sectionColor = "white", 
  borderColor = "black", 
  borderWidth = "1px", 
  margin = "0%",
  padding = "0%", }) => {
  return (
    <Box
      sx={{
        "display": "flex",
        "flexDirection": "column",
        "margin": {
          "xs": "0%",
          "sm": margin,
        },
        "padding": padding,
        "border": `${borderWidth} solid ${borderColor}`,
        "backgroundColor": sectionColor,
      }}
    >
      {children}
    </Box>
  );
};

export default Section;