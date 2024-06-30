<script lang="ts">
  import { createTable, Render, Subscribe, createRender } from 'svelte-headless-table';
  import { writable } from 'svelte/store';
  import * as Table from '$lib/elements/ui/table';
  import DataTableActions from './data-table-actions.svelte';
  import {
    addPagination,
    addSortBy,
    addTableFilter,
    addHiddenColumns,
    addSelectedRows,
  } from 'svelte-headless-table/plugins';
  import { Button } from '$lib/elements/ui/button';
  import ArrowUpDown from 'lucide-svelte/icons/arrow-up-down';
  import ChevronDown from 'lucide-svelte/icons/chevron-down';
  import { Input } from '$lib/elements/ui/input';
  import * as DropdownMenu from '$lib/elements/ui/dropdown-menu';
  import DataTableCheckbox from './data-table-checkbox.svelte';

  interface DataItem {
    id: string;

    [key: string]: string;
  }

  export let data: DataItem[];
  export let columnNames;

  const table = createTable(writable(data), {
    page: addPagination(),
    sort: addSortBy(),
    filter: addTableFilter({
      fn: ({ filterValue, value }) => value.toLowerCase().includes(filterValue.toLowerCase()),
    }),
    hide: addHiddenColumns(),
    select: addSelectedRows(),
  });

  let idKey = Object.keys(data[0])[0];
  console.log(idKey);
  let columns = table.createColumns([
    table.column({
      //first column only contains the checkboxes.
      accessor: (item) => {
        return item[idKey]; //'item' is of type 'unknown' error dont know how to fix
      },
      id: 'id',
      header: (_, { pluginStates }) => {
        const { allPageRowsSelected } = pluginStates.select;
        return createRender(DataTableCheckbox, {
          checked: allPageRowsSelected,
        });
      },
      cell: ({ row }, { pluginStates }) => {
        const { getRowState } = pluginStates.select;
        const { isSelected } = getRowState(row);

        return createRender(DataTableCheckbox, {
          checked: isSelected,
        });
      },
      plugins: {
        sort: {
          disable: true,
        },
      },
    }),
  ]);
  for (const [i, columnName] of columnNames.entries()) {
    let currentKey = Object.keys(data[0])[i + 1];
    columns = columns.concat(
      table.createColumns([
        table.column({
          accessor: (item: DataItem) => {
            return item[currentKey];
          },
          header: columnName,
        }),
      ]),
    );
  }

  columns = columns.concat(
    table.createColumns([
      table.column({
        accessor: (item) => {
          return item[idKey];
        },
        header: '',
        id: 'actions',
        cell: ({ value }) => {
          return createRender(DataTableActions, { id: value });
        },
        plugins: {
          sort: {
            disable: true,
          },
        },
      }),
    ]),
  );

  const { headerRows, pageRows, tableAttrs, tableBodyAttrs, pluginStates, flatColumns, rows } =
    table.createViewModel(columns);
  const { hasNextPage, hasPreviousPage, pageIndex } = pluginStates.page;
  const { filterValue } = pluginStates.filter;
  const { hiddenColumnIds } = pluginStates.hide;
  const { selectedDataIds } = pluginStates.select;

  const ids = flatColumns.map((col) => col.id);
  let hideForId = Object.fromEntries(ids.map((id) => [id, true]));

  $: $hiddenColumnIds = Object.entries(hideForId)
    .filter(([, hide]) => !hide)
    .map(([id]) => id);

  const hidableCols = ['status', 'email', 'amount'];
</script>

<div>
  <div class="flex items-center py-4">
    <Input bind:value={$filterValue} class="max-w-sm" placeholder="Suche..." type="text" />
    <DropdownMenu.Root>
      <DropdownMenu.Trigger asChild let:builder>
        <Button builders={[builder]} class="ml-auto" variant="outline">
          Filter
          <ChevronDown class="ml-2 h-4 w-4" />
        </Button>
      </DropdownMenu.Trigger>
      <DropdownMenu.Content>
        {#each flatColumns as col}
          {#if hidableCols.includes(col.id)}
            <DropdownMenu.CheckboxItem bind:checked={hideForId[col.id]}>
              {col.header}
            </DropdownMenu.CheckboxItem>
          {/if}
        {/each}
      </DropdownMenu.Content>
    </DropdownMenu.Root>
  </div>
  <div class="rounded-md border">
    <Table.Root {...$tableAttrs}>
      <Table.Header>
        {#each $headerRows as headerRow}
          <Subscribe rowAttrs={headerRow.attrs()}>
            <Table.Row>
              {#each headerRow.cells as cell (cell.id)}
                <Subscribe attrs={cell.attrs()} let:attrs props={cell.props()} let:props>
                  <Table.Head {...attrs} class="[&:has([role=checkbox])]:pl-3">
                    {#if cell.id !== 'actions' && cell.id !== 'id'}
                      <Button variant="ghost" on:click={props.sort.toggle}>
                        <Render of={cell.render()} />
                        <ArrowUpDown class={'ml-2 h-4 w-4'} />
                      </Button>
                    {:else}
                      <Render of={cell.render()} />
                    {/if}
                  </Table.Head>
                </Subscribe>
              {/each}
            </Table.Row>
          </Subscribe>
        {/each}
      </Table.Header>
      <Table.Body {...$tableBodyAttrs}>
        {#each $pageRows as row (row.id)}
          <Subscribe rowAttrs={row.attrs()} let:rowAttrs>
            <Table.Row {...rowAttrs} {...rowAttrs} data-state={$selectedDataIds[row.id] && 'selected'}>
              {#each row.cells as cell (cell.id)}
                <Subscribe attrs={cell.attrs()} let:attrs>
                  <Table.Cell {...attrs}>
                    <Render of={cell.render()} />
                  </Table.Cell>
                </Subscribe>
              {/each}
            </Table.Row>
          </Subscribe>
        {/each}
      </Table.Body>
    </Table.Root>
  </div>
  <div class="flex items-center justify-end space-x-4 py-4">
    <div class="flex-1 text-sm text-muted-foreground">
      {Object.keys($selectedDataIds).length} von{' '}
      {$rows.length} Zeile(n) ausgewählt.
    </div>
    <Button disabled={!$hasPreviousPage} on:click={() => ($pageIndex = $pageIndex - 1)} size="sm" variant="outline"
    >Zurück
    </Button
    >
    <Button disabled={!$hasNextPage} on:click={() => ($pageIndex = $pageIndex + 1)} size="sm" variant="outline"
    >Weiter
    </Button
    >
  </div>
</div>
