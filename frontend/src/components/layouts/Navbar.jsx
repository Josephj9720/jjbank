import { Box, AppBar, Toolbar } from '@mui/material'
import NavbarLink from './NavbarLink';
import { Lock, LockOpen } from '@mui/icons-material';
import { useLocation } from 'react-router';
import VerticalDivider from './VerticalDivider';
import { useAuthContext } from '../../hooks/useAuthentication';
import { FRONT_END_ROUTES } from '../../util/routes';
import NavbarIcon from './NavbarIcon';

export default function Navbar() {

  const location = useLocation();

  const { authDetails } = useAuthContext();

  const isCurrentRoute = (route) => {
    return route === location.pathname;
  }

  return (
    
      <Box sx={{flexGrow: 1}}>
        <AppBar position='fixed'>
          {/* alignItems strech on the toolbar makes the content fill the height of the toolbar (secondary axis) */}
          <Toolbar sx={{
            alignItems: "stretch", 
            backgroundColor: (theme) => theme.palette.navbar.background}}
          >
            <Box sx={{ display: "flex", marginRight: (theme) => theme.spacing(3) }}>
              <NavbarIcon 
                asset={"appIcon"}
                assetAria={"appIconAria"}
                destination={FRONT_END_ROUTES.HOME}
              />
            </Box>
            <Box sx={{ display: "flex", gap: 1, marginLeft: (theme) => theme.spacing(3) }}>
              {
                authDetails && <>
                  <NavbarLink
                    text={"Dashboard"}
                    destination={FRONT_END_ROUTES.DASHBOARD}
                    isActive={isCurrentRoute(FRONT_END_ROUTES.DASHBOARD)}
                  />
                  <VerticalDivider />
                  <NavbarLink
                    text={"Transfer"}
                    destination={FRONT_END_ROUTES.TRANSFER}
                    isActive={isCurrentRoute(FRONT_END_ROUTES.TRANSFER)}
                  />
                </>
              }
            </Box>
            <Box sx={{marginLeft: "auto", display: "flex", gap: 1}}>
              <VerticalDivider />
              {
                authDetails || isCurrentRoute(FRONT_END_ROUTES.LOGOUT)
                ?
                  <NavbarLink
                    icon={LockOpen}
                    text={"Logout"}
                    destination={FRONT_END_ROUTES.LOGOUT}
                    isActive={isCurrentRoute(FRONT_END_ROUTES.LOGOUT)}
                  />
                :
                  <NavbarLink
                    icon={Lock}
                    text={"Login"}
                    destination={FRONT_END_ROUTES.LOGIN}
                    isActive={isCurrentRoute(FRONT_END_ROUTES.LOGIN)}
                  />
              }
            </Box>
          </Toolbar>
        </AppBar>
      </Box>

  );
};