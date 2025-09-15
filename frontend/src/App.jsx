import './App.css'
import Router from './components/Router'
import { ThemeProvider } from './components/ThemeProvider'
import { CssBaseline } from '@mui/material'

function App() {

  return (
    <>
    <ThemeProvider>
      <CssBaseline/>
      <Router/>
    </ThemeProvider>
    </>
  )
}

export default App
