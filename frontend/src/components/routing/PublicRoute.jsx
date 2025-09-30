import { Navigate, useLocation } from "react-router";
import { useAuthContext } from "../../hooks/useAuthentication";
import { FRONT_END_ROUTES } from "../../util/routes";

function PublicRoute({ children }) {
  const { authDetails } = useAuthContext();
  const location = useLocation();
  const from = location.state?.from?.pathname;

  //redirect to dashboard if user is authenticated
  if(authDetails) {
    return from ? <Navigate to={from} replace /> : <Navigate to={FRONT_END_ROUTES.DASHBOARD} replace /> ;
  }

  //otherwise, render the children (login or register)
  return children;
}

export default PublicRoute;