import { Box, useTheme, Link } from "@mui/material";
import { Link as ReactRouterLink } from "react-router";


/* optinally use values from the theme { asset, assetAria } but prefer
  directly specified values { path, aria } 
  optionally move to destination onclick if specified
  */
const NavbarIcon = ({ asset, assetAria, path, aria, destination }) => {

  const theme = useTheme();

  const iconSrc = path ? path : theme.assets.navbar[asset];

  const iconAriaLabel = aria ? aria : theme.assets.navbar[assetAria];

  const containerStyles = {
    display: "flex",
    alignItems: "center",

  };

  const iconStyles = {
    width: "50px",
    borderRadius: "10px"
  };

  const iconBoxStyles = {
    borderRadius: "10px",
    ":hover": {
      border: `1px solid ${theme.palette.navbar.text}`
    }
  }

  return (
    <Link
      component={ReactRouterLink}
      to={destination}
      sx={ containerStyles }
    >
      <Box
        sx={iconBoxStyles}
      >
        <img 
          src={ iconSrc }
          style={ iconStyles }
          aria-label={ iconAriaLabel }
        />
      </Box>
    </Link>
  );

}

export default NavbarIcon;