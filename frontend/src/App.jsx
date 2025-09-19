import './App.css'
import Router from './components/Router'
import { ThemeProvider } from './components/ThemeProvider'
import { CssBaseline } from '@mui/material'
import { AuthenticationProvider } from './components/AuthenticationProvider'

function App() {

  return (
    <>
      <AuthenticationProvider>
        <ThemeProvider>
          <CssBaseline/>
          <Router/>
        </ThemeProvider>
      </AuthenticationProvider>
    </>
  )
}

export default App
