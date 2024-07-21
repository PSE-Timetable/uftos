<script lang="ts">
  import { icons } from 'lucide-svelte';
  import Card from '../../lib/components/ui/card/card.svelte';
  import { Icons } from '$lib/utils';
  import Link from '$lib/elements/ui/link/link.svelte';
  import LinkBar from '$lib/components/ui/link-bar/link-bar.svelte';
</script>

<div class="h-[150px] w-full bg-primary text-white p-4 font-medium text-2xl absolute">
  <div class="pb-4">
    <LinkBar />
  </div>
  <div class="w-full flex flex-row gap-14 justify-between">
    <Card text="Schüler" icon={Icons.STUDENT} />
    <Card text="Lehrer" icon={Icons.TEACHER} />
    <Card text="Stufen" icon={Icons.CLASS} />
    <Card text="Räume" icon={Icons.ROOM} />
    <Card text="Constraints" icon={Icons.CONSTRAINT} />
  </div>
</div>
