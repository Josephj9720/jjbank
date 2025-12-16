import { Outlet } from "react-router";
import Navbar from "./Navbar";
import MainContent from "./MainContent";
import { Box, Toolbar } from "@mui/material";
import Footer from "./Footer";

const Layout = () => {
  return (
    <Box 
      sx={{ 
        "display" : "flex", 
        "flexDirection" : "column",
        "height" : "100vh",
      }}
    >
      <Navbar /> {/* same navbar on every page */}
      <MainContent>
        <Outlet /> {/* Renders the current page component */}
      </MainContent>
      <Footer />
    </Box>
  );
}

export default Layout;  