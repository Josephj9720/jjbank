import { createContext, useCallback, useContext, useEffect, useState } from "react";
import { FRONT_END_ROUTES } from "../util/routes";
import api from "../util/apiClient";
import { API_ENDPOINTS } from "../util/endpoints";

//create context object with default value
export const AuthenticationContext = createContext();

//custom hook
//returns the prop value from the nearest context provider
export const useAuthContext = () => useContext(AuthenticationContext);

export const useRefreshToken = () => {

  const [authDetails, setAuthDetails] = useState(null);
  const [isAuthenticating, setIsAuthenticating] = useState(true);

  //returns a memoized version of the method unless one of the inputs has changed
  const refreshToken = useCallback(() => {
    //check if the authDetails object exists and has an access token, if it exists, don't refresh
    if(authDetails && authDetails.accessToken) {
      return;
    }

    console.log("Attempting to refresh token...");

    api.post(API_ENDPOINTS.REFRESH, null, {
      withCredentials: true //this allows to send the cookie
    })
    .then(response => {
      //success: update the auth details in the global state
      setAuthDetails({
        fullName: response.data.fullName,
        accessToken: response.data.accessToken,
      });
    })
    .catch(error => {
      console.log("Failed to refresh token", error);
      //failure: clear auth details (log out user)
      setAuthDetails(null);
    })
    .finally(() => {
      setIsAuthenticating(false);
    });

  }, [authDetails]); //dependency array to prevent re-creation
  
  useEffect(() => {
    refreshToken();
  }, [refreshToken]);

  return [authDetails, setAuthDetails, isAuthenticating];
}