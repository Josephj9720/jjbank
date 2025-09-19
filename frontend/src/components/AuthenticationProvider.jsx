import { AuthenticationContext, useRefreshToken } from "../hooks/useAuthentication";

//holds the state for the authentication data, initially blank
export const AuthenticationProvider = ({ children }) => {

  //call hook unconditionally in provider to refresh token globally
   const [authDetails, setAuthDetails] = useRefreshToken();

  //value given to the context provider
  //it contains the authentication details, and the method to update the details
  const value = {
    authDetails,
    setAuthDetails,
  }

  return (
    <AuthenticationContext.Provider value={value}>
      {children}
    </AuthenticationContext.Provider>
  );
}