import { Button, CardContent, Typography } from "@mui/material";
import useTitle from "../../hooks/useTitle";
import AuthCard from "../layouts/AuthCard";
import { useCallback, useEffect, useState } from "react";
import LogoutIcon from "@mui/icons-material/Logout";
import { useNavigate } from "react-router";
import { FRONT_END_ROUTES } from "../../util/routes";
import { useAuthContext } from "../../hooks/useAuthentication";
import api from "../../util/apiClient";
import { API_ENDPOINTS } from "../../util/endpoints";


const Logout = () => {
  useTitle("Logout | JJ Bank");

  //time before user is redirected to home page
  const [redirectTime, setRedirectTime] = useState(5);

  const { setAuthDetails } = useAuthContext();

  const navigate = useNavigate();

  //returns a memoized version of the method, prevents dependencies of 
  //useEffect from chaging on every render
  //functions defined directly in the scope of a component are recreated on every render
  const redirectToHome = useCallback(() => {
    navigate(FRONT_END_ROUTES.HOME, {replace: true});
  }, [navigate]);

  useEffect(() => {
    //make call to backend to logout the user
    api.post(API_ENDPOINTS.LOGOUT, null, {
      withCredentials: true // this allows to send the cookie
    })
    .then(() => {
      //successful logout: clear auth details from context
      console.log("successful logout");
      setAuthDetails(null);
    })
    .catch(error => {
      console.log("Failed to logout user", error);
    });

  }, [setAuthDetails]);

  useEffect(() => {

    const intervalId = setInterval(() => {
      //when redirect time runs out, redirect to home page
      if(redirectTime == 0) redirectToHome();

      //decrement redirect time by 1
      setRedirectTime(time => time - 1);
    }, 1000);

    //clear interval on re-render to avoid memory leaks
    return () => clearInterval(intervalId);

    //add redirectTime as dependency to re-run effect when it is updated
    //add redirectToHome as dependency for correctness even if unchanging
  }, [redirectTime, redirectToHome]);

  return (
    <AuthCard>
      <CardContent 
        sx={{
          display: "flex",
          flexDirection: "column",
          justifyContent: "center",
          alignItems: "center",
          marginTop: "25%",
          height: "50%",
        }}
      >
        <LogoutIcon 
          color="secondary"
          fontSize="large"
        />
        <Typography
          variant="h4"
          marginBottom={"5%"}
        >
          Logging out...
        </Typography>
        <Typography
          variant="h5"
          marginBottom={"1%"}
        >
          You will be redirected in {redirectTime} second{redirectTime > 1? "s" : ""}.
        </Typography>
        <Button
          variant="contained"
          color="secondary"
          onClick={redirectToHome}
        >
          Log out
        </Button>

      </CardContent>
    </AuthCard>
  );

}

export default Logout;