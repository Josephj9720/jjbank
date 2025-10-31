import { useState } from "react";
import { useAuthContext } from "../../hooks/useAuthentication";
import { FRONT_END_ROUTES } from "../../util/routes";
import { Box, useTheme } from "@mui/material";
import Banner from "../layouts/Banner";

const Dashboard = () => {
  const [accounts, setAccounts] = useState(null);

  const { authDetails } = useAuthContext();

  const theme = useTheme();



  
  

  return (
    <Box
      sx={{
        "display" : "flex",
        "flexDirection" : "column",
        "width" : "100%",
        "padding" : "0% 0%",
        
      }}
    >
      <Banner
        text={"Welcome " + authDetails.fullName}
        textColor={ theme.palette.welcomeBanner.text }
        bannerColor={ theme.palette.welcomeBanner.background }
        borderColor={ theme.palette.welcomeBanner.border }
        borderWidth={ theme.layout.welcomeBanner.borderWidth }
        margin="0 0 2% 0"      
      />

    </Box>
  );
};

export default Dashboard;