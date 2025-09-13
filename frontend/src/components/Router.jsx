import { Routes, Route } from 'react-router';
import { ENDPOINTS } from '../util/endpoints';

function Router() {
  return (
    <Routes>
      <Route path={ENDPOINTS.HOME} element={<p>home</p>}/>
      <Route path={ENDPOINTS.AUTH} element={<p>auth</p>}/>
      <Route path={ENDPOINTS.ACCOUNTS} element={<p>accounts</p>}/>
      <Route path={ENDPOINTS.TRANSACTIONS} element={<p>transactions</p>}/>
      <Route path={ENDPOINTS.TRANSFERS} element={<p>transfer</p>}/>
    </Routes>
  );
}

export default Router;