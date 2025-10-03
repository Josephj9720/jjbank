import { Link, Typography, Box } from "@mui/material";
import { Link as ReactRouterLink } from "react-router";


const NavDrawerLink = ({ icon: IconComponent, text, destination, isActive }) => {
  
  const inactiveStyles = {
    color: (theme) => theme.palette.navbar.text,
    display: "flex",
    flexGrow: 1,
    justifyContent: "center",
    gap: "1",
    paddingY: "15px",
    textAlign: "center",
    textDecoration: "none",
    borderRadius: "10px",
    ":hover": {
      backdropFilter: "brightness(55%)",
    },
  };

  const activeStyles = {
    color: (theme) => theme.palette.navbar.text,
    backdropFilter: "brightness(55%)",
    display: "flex",
    flexGrow: 1,
    justifyContent: "center",
    gap: "1",
    paddingY: "15px",
    textAlign: "center",
    textDecoration: "none",
    borderRadius: "10px",
  };

  return (
    <Box
      display={"flex"}
      justifyContent={"center"}
    >
      <Link
        component={ReactRouterLink}
        to={destination}
        sx={ isActive ? activeStyles : inactiveStyles }
      >
        { IconComponent && <IconComponent /> }
        <Typography variant="subtitle1">{text}</Typography>
      </Link>
    </Box>
  );
};

export default NavDrawerLink;