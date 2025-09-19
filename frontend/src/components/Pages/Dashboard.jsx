import { useState } from "react";
import { useAuthContext, useRefreshToken } from "../../hooks/useAuthentication";
import { FRONT_END_ROUTES } from "../../util/routes";

const Dashboard = () => {
  const [accounts, setAccounts] = useState(null);


  const auth = useAuthContext();




  
  

  return (
    <div>
      {!accounts && <p>Loading...</p>}
      {!!accounts && <p>Dashboard</p>}
    </div>
  );
}

export default Dashboard;