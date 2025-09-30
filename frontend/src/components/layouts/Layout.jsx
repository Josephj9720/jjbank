import { Outlet } from "react-router";
import Navbar from "./Navbar";
import { Toolbar } from "@mui/material";
import Footer from "./Footer";

const Layout = () => {
  return (
    <>
      <Navbar /> {/* same navbar on every page */}
      <Toolbar /> {/* for spacing, so that content start below main toolbar */}
      <main>
        <Outlet /> {/* Renders the current page component */}
      </main>
      <Footer />
    </>
  );
}

export default Layout;