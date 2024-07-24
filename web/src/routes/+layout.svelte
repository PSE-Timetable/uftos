<script>
  import { Toaster } from 'svelte-sonner';
  import '../app.css';
</script>

<slot />
<Toaster class="text-primary" />
