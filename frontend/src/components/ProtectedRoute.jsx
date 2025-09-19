import { Navigate, useLocation } from "react-router";
import { useAuthContext } from "../hooks/useAuthentication";
import { FRONT_END_ROUTES } from "../util/routes";

function ProtectedRoute({ children }) {
  const { authDetails } = useAuthContext();
  const location = useLocation(); //get current location

  //if authDetails don't exist, redirect to login page
  //but save current location in state
  if(!authDetails) {
    return <Navigate to={FRONT_END_ROUTES.LOGIN} state={{ from: location }} replace />
  }

  //otherwise, render children (the protected page)
  return children;

}

export default ProtectedRoute;