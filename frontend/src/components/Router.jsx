import { Routes, Route } from 'react-router';
import { FRONT_END_ROUTES } from '../util/routes';

function Router() {
  return (
    <Routes>
      <Route path={FRONT_END_ROUTES.HOME} element={<p>home</p>}/>
      <Route path={FRONT_END_ROUTES.LOGIN} element={<p>login</p>}/>
      <Route path={FRONT_END_ROUTES.REGISTER} element={<p>register</p>}/>
      <Route path={FRONT_END_ROUTES.DASHBOARD} element={<p>dashboard</p>}/>
      <Route path={FRONT_END_ROUTES.ACCOUNTS} element={<p>accounts</p>}/>
      <Route path={FRONT_END_ROUTES.TRANSFER} element={<p>transfer</p>}/>
    </Routes>
  );
}

export default Router;