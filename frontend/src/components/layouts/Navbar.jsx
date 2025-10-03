import { Box, AppBar, Toolbar, IconButton, Drawer, Divider } from '@mui/material'
import NavbarLink from './NavbarLink';
import { Lock, LockOpen, Menu } from '@mui/icons-material';
import { useLocation } from 'react-router';
import VerticalDivider from './VerticalDivider';
import { useAuthContext } from '../../hooks/useAuthentication';
import { FRONT_END_ROUTES } from '../../util/routes';
import NavbarIcon from './NavbarIcon';
import { useState } from 'react';
import NavDrawerLink from './NavDrawerLink';

export default function Navbar() {

  const location = useLocation();

  const { authDetails } = useAuthContext();

  const isCurrentRoute = (route) => {
    return route === location.pathname;
  }

  //nav drawer state
  const [isOpen, setIsOpen] = useState(false);

  const toggleDrawer = () => {
    setIsOpen(prevState => !prevState);
  };

  const LeftNavMenu = () => {
    return (
      <>
        <Box sx={{ 
          display: {
            sm: "flex",
            xs: "none",
          }, 
          gap: 1, 
          marginLeft: (theme) => theme.spacing(3) 
        }}>
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
      </>
    );
  };
  
  const RightNavMenu = () => {
    return (
      <>
        <Box sx={{ marginLeft: "auto", display: "flex", gap: 1 }}>
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
      </>
    );
  };

  const drawerContent = (
    <Box
      onClick={toggleDrawer}
      display={"flex"}
      flexDirection={"column"}
      alignItems={"stretch"}
    >
      <Box 
        paddingY={"10px"}
        display={"flex"}
        justifyContent={"center"}
      >
        <NavbarIcon 
          asset={"appIcon"}
          assetAria={"appIconAria"}
          destination={FRONT_END_ROUTES.HOME}
        />
      </Box>
      <Divider
        flexItem
        sx={{ 
          background: (theme) => theme.palette.navbar.text, 
        }}
      />
      {
        authDetails && <>
          <NavDrawerLink
            text={"Dashboard"}
            destination={FRONT_END_ROUTES.DASHBOARD}
            isActive={isCurrentRoute(FRONT_END_ROUTES.DASHBOARD)}
          />
          <NavDrawerLink
            text={"Transfer"}
            destination={FRONT_END_ROUTES.TRANSFER}
            isActive={isCurrentRoute(FRONT_END_ROUTES.TRANSFER)}
          />
        </>
      }
    </Box>
  );

  const NavDrawer = () => {
    return (
      <Drawer
        slotProps={{ 
          paper: {
            sx: {
              // must use background property to override the property of the MUI Paper component
              // to make background color lighter with more elevation
              background: (theme) => theme.palette.navbar.background,
              width: {
                xs: "100vw",
                sm: "240px",
              }
            }
          } 
        }}
        open={isOpen}
        onClose={toggleDrawer}
        ModalProps={{ keepMounted: true }} //better open performance on mobile according to MUI
        sx={{
          display: {
            xs: "block", 
            sm: "none",
          },
        }}
      >
        { drawerContent }
      </Drawer>
    );
  };

  return (
    
      <Box sx={{flexGrow: 1}}>
        <AppBar position='fixed'>
          {/* alignItems strech on the toolbar makes the content fill the height of the toolbar (secondary axis) */}
          <Toolbar sx={{
            alignItems: "stretch", 
            backgroundColor: (theme) => theme.palette.navbar.background}}
          >
            <Box
              sx={{ 
                display: {
                  xs: "flex",
                  sm: "none",
                }, 
                marginRight: (theme) => theme.spacing(3) 
              }}
            >
              <IconButton
                onClick={toggleDrawer}
                size="small"
                disableRipple
                sx={{
                  color: (theme) => theme.palette.navbar.text,
                }}
              >
                <Menu fontSize="large" />
              </IconButton>
            </Box>
            <Box sx={{ display: "flex", marginRight: (theme) => theme.spacing(3) }}>
              <NavbarIcon 
                asset={"appIcon"}
                assetAria={"appIconAria"}
                destination={FRONT_END_ROUTES.HOME}
              />
            </Box>
            <LeftNavMenu
              isAuthenticated={authDetails}
              FRONT_END_ROUTES={FRONT_END_ROUTES}
              isCurrentRoute={isCurrentRoute}
            />
            <RightNavMenu
              isAuthenticated={authDetails}
              FRONT_END_ROUTES={FRONT_END_ROUTES}
              isCurrentRoute={isCurrentRoute}
            />
          </Toolbar>
        </AppBar>
        <NavDrawer 
        
        />
      </Box>

  );
};