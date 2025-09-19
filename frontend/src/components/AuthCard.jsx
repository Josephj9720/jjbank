import { Box, Card } from '@mui/material';

const AuthCard = ({ children }) => {

  return (
    <Box
      display={"flex"}
      height={"93.5vh"}
      justifyContent={"center"}
      padding={"1%"}
    >
      <Card
        sx={{
          "width": {
            "xs" : "100%",
            "sm" : "700px",
            "lg" : "800px",
            "xl" : "900px"
          },
          "padding": "2%",
          "textAlign": "center",
          "borderRadius": "2%",
        }}
      >
        { children }
      </Card>
    </Box>
  );

}

export default AuthCard;