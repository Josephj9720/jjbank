import { Routes, Route } from 'react-router';
import { FRONT_END_ROUTES } from '../util/routes';
import Layout from './Layout';
import Register from './Pages/Register';
import Login from './Pages/Login';
import Dashboard  from './Pages/Dashboard';
import PublicRoute from './PublicRoute';
import ProtectedRoute from './ProtectedRoute';
import Logout from './Pages/Logout';

function Router() {
  
  return (
    <Routes>
      <Route path={FRONT_END_ROUTES.HOME} element={<Layout/>}>
        <Route index element={<p>home</p>}/> {/* index is the default content of the parent route */}
        <Route path={FRONT_END_ROUTES.LOGIN} element={<PublicRoute><Login/></PublicRoute>}/>
        <Route path={FRONT_END_ROUTES.REGISTER} element={<PublicRoute><Register/></PublicRoute>}/>
        <Route path={FRONT_END_ROUTES.DASHBOARD} element={<ProtectedRoute><Dashboard/></ProtectedRoute>}/>
        <Route path={FRONT_END_ROUTES.ACCOUNTS} element={<ProtectedRoute><p>accounts</p></ProtectedRoute>}/>
        <Route path={FRONT_END_ROUTES.TRANSFER} element={<ProtectedRoute><p>transfer</p></ProtectedRoute>}/>
        <Route path={FRONT_END_ROUTES.LOGOUT} element={<Logout/>}></Route>
      </Route>
    </Routes>
  );
}

export default Router;