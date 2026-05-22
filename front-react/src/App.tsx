import { AppRouter } from './router';
import { useAuthBootstrap } from './hooks/useAuth';

export default function App() {
  useAuthBootstrap();
  return <AppRouter />;
}
