import { Box, Typography } from "@mui/material";

const Banner = ({ 
  text = "default", 
  textColor = "black", 
  bannerColor = "white", 
  borderColor = "black", 
  borderWidth = "1px" }) => {

  return (
    <Box
      sx={{
        "display" : "flex",
        "width" : "100%",
        "textAlign" : "left",
        "color" : textColor,
        "backgroundColor" : bannerColor,
        "borderColor" : borderColor,
        "borderBottom" : `${borderWidth} solid`,
        "padding" : "3.5%"
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