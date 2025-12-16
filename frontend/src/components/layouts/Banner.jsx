import { Box, Typography } from "@mui/material";

const Banner = ({ 
  text = "default", 
  textColor = "black", 
  bannerColor = "white", 
  borderColor = "black", 
  borderWidth = "1px",
  margin = "0" }) => {

  return (
    <Box
      sx={{
        "display" : "flex",
        "width" : "100%",
        "textAlign" : "left",
        "color" : textColor,
        "backgroundColor" : bannerColor,
        "borderBottom" : `${borderWidth} solid ${borderColor}`,
        "padding" : "3.5%",
        "margin" : margin,
      }}
    >
      <Typography
        variant="h5"
      >
        <b>{text}</b>
      </Typography>
    </Box>
  );

};

export default Banner;