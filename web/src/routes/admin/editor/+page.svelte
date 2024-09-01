<script>
  import { goto } from '$app/navigation';
  import { page } from '$app/stores';
  import Editor from '$lib/components/editor.svelte';
  import { Button } from '$lib/elements/ui/button';
  import Navbar from '$lib/elements/ui/navbar/navbar.svelte';
  import { ChevronLeft } from 'lucide-svelte';
</script>

<Navbar />

<div class="flex justify-center items-center p-4">
  <Editor />
</div>
