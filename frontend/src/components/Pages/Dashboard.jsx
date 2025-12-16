import { useCallback, useEffect, useState } from "react";
import { useAuthContext } from "../../hooks/useAuthentication";
import { FRONT_END_ROUTES } from "../../util/routes";
import { Box, Divider, Typography, useTheme } from "@mui/material";
import Banner from "../layouts/Banner";
import Section from "../layouts/Section";
import SectionRow from "../layouts/SectionRow";
import api from "../../util/apiClient";
import { API_ENDPOINTS } from "../../util/endpoints";
import { Link as ReactRouterLink } from "react-router";
import { AddCircleOutlineOutlined } from "@mui/icons-material";

const Dashboard = () => {
  const [accounts, setAccounts] = useState([]);

  const ACCOUNT_NUMBER_LIMIT = 3;

  const { authDetails } = useAuthContext();

  const MY_ACCOUNTS = "/me";


  //memoized version of the method to retrieve accounts
  const getAccounts = useCallback(() => {

    const config = {
      headers: { "Authorization": `Bearer ${authDetails.accessToken}` },
    };

    api.get(API_ENDPOINTS.ACCOUNTS + MY_ACCOUNTS, config)
    .then(response => {
      console.log(response.data);
      setAccounts(response.data);

    })
    .catch(error => {
      console.log("Failed to fetch data.", error); //how 
    });


  }, [authDetails]);

  //fetch account data, synch component with external system (backend)
  useEffect(() => {
    getAccounts();
  }, [getAccounts]);

  const theme = useTheme();

  const accountListItems = accounts.map(account => 
    <div key={account.id}>
      <SectionRow
        component={ReactRouterLink}
        componentProps={{
          to: `${FRONT_END_ROUTES.ACCOUNTS}/${account.id}`,
        }}
      >
        <Typography
          variant="body1"
          color={ theme.palette.section.secondaryText }
        >
          <b>JJB {account.type} ACCOUNT </b>
        </Typography>
        <Typography
          variant="body1"
          color={ theme.palette.section.secondaryText }
          marginLeft={"1%"}
        >
          <i>{account.id}</i>
        </Typography>
        <Typography
          variant="body1"
          color={ theme.palette.section.secondaryText }
          sx={{
            "marginLeft": "auto",
          }}
        >
          {account.balance.toFixed(2)} $J
        </Typography>
      </SectionRow>
      <Divider
        sx={{
          "margin": "1% 0%",
        }}
      />
    </div>
  );

  const accountSumOfBalances = accounts.reduce(
    (sum, currentAccount) => sum + currentAccount.balance,
    0
  ).toFixed(2);

  
  

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

      <Section
        sectionColor={ "transparent" }
        borderColor={ "transparent" }
        borderWidth={ "0px" }
        margin={ theme.layout.section.alignment.right }
      >
        <Typography
          variant="h5"
          color={ theme.palette.section.primaryText }
        >
          <b>My Accounts</b>
        </Typography>
      </Section>

      <Section
        sectionColor={ theme.palette.section.background }
        borderColor={ theme.palette.section.border }
        borderWidth={ theme.layout.section.borderWidth }
        margin={ theme.layout.section.alignment.right }
        padding="1%"
      >
        <SectionRow>
          <Typography
            variant="body1"
            color={ theme.palette.section.primaryText }
          >
            <b>Banking Services</b>
          </Typography>
          <Typography
            variant="body1"
            color={ theme.palette.section.primaryText }
            sx={{
              "marginLeft": "auto",
            }}
          >
            <b>{ accountSumOfBalances } $J</b>
          </Typography>
        </SectionRow>
        <Divider
          sx={{
            "margin": "1% 0%",
            "backgroundColor": theme.palette.section.primaryText,
          }}
        />
        {accountListItems}
      </Section>

      { accounts.length < ACCOUNT_NUMBER_LIMIT && <Section
        component={ ReactRouterLink }
        componentProps={{
          to: FRONT_END_ROUTES.CREATE,
        }}
        sectionColor={ theme.palette.section.background }
        borderColor={ theme.palette.section.border }
        borderWidth={ theme.layout.section.borderWidth }
        margin={ theme.layout.section.alignment.right }
        padding="0.5%"
      >
        <Typography
          variant="body1"
          color={ theme.palette.section.secondaryText }
          sx={{
            "display": "flex",
            "alignItems": "center",
            "justifyContent": "center",
            "gap": "0.5%"
          }}
        >
          <AddCircleOutlineOutlined/>
          Create additonal account
        </Typography>

      </Section>}

    </Box>
  );
};

export default Dashboard;