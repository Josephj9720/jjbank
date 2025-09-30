import { Box } from "@mui/material";

const VerticalDivider = ({width = "1px"}) => {

  const containerStyles = {
    display: "flex",
    alignItems: "center"
  };

  const dividerStyles = {
    height: "60%",
    border: (theme) => `${width} solid ${theme.palette.navbar.text}`
  };

  return (
    <Box sx={containerStyles}>
      <Box sx={dividerStyles}></Box>
    </Box>
  );
}

export default VerticalDivider;