import { Routes, Route } from 'react-router';
import { FRONT_END_ROUTES } from '../util/routes';
import Layout from './Layout';
import Register from './Pages/Register';
import Login from './Pages/Login';

function Router() {
  return (
    <Routes>
      <Route path={FRONT_END_ROUTES.HOME} element={<Layout/>}>
        <Route index element={<p>home</p>}/> {/* index is the default content of the parent route */}
        <Route path={FRONT_END_ROUTES.LOGIN} element={<Login/>}/>
        <Route path={FRONT_END_ROUTES.REGISTER} element={<Register/>}/>
        <Route path={FRONT_END_ROUTES.DASHBOARD} element={<p>dashboard</p>}/>
        <Route path={FRONT_END_ROUTES.ACCOUNTS} element={<p>accounts</p>}/>
        <Route path={FRONT_END_ROUTES.TRANSFER} element={<p>transfer</p>}/>
      </Route>
    </Routes>
  );
}

export default Router;