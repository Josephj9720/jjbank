import { Link, Typography } from "@mui/material";
import { Link as ReactRouterLink } from "react-router";

const NavbarLink = ({ icon: IconComponent, text, destination, isActive }) => {

  const inactiveStyles = {
    color: (theme) => theme.palette.navbar.text,
    display: "flex",
    alignItems: "center",
    gap: "1",
    paddingX: "15px",
    textAlign: "center",
    textDecoration: "none",
    ":hover": {
      borderBottom: (theme) => `4px solid ${theme.palette.navbar.text}`
    },
  };

  const activeStyles = {
    color: (theme) => theme.palette.navbar.text,
    backgroundColor: (theme) => theme.palette.primary.main,
    borderBottom: (theme) => `4px solid ${theme.palette.navbar.text}`,
    display: "flex",
    alignItems: "center",
    gap: "1",
    paddingX: "15px",
    textAlign: "center",
    textDecoration: "none",
  };

  return (
    <Link
      component={ReactRouterLink}
      to={destination}
      sx={ isActive ? activeStyles : inactiveStyles }
    >
      {IconComponent && <IconComponent />}
      <Typography variant="subtitle1">{text}</Typography>
    </Link>
  );
}

export default NavbarLink;