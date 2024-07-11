<script lang="ts">
  import { createTable, Render, Subscribe, createRender } from 'svelte-headless-table';
  import * as Table from '$lib/elements/ui/table';
  import DataTableActions from './data-table-actions.svelte';
  import {
    addPagination,
    addSortBy,
    addTableFilter,
    addHiddenColumns,
    addSelectedRows,
    type SortKey,
  } from 'svelte-headless-table/plugins';
  import { Button } from '$lib/elements/ui/button';
  import ChevronDown from 'lucide-svelte/icons/chevron-down';
  import * as DropdownMenu from '$lib/elements/ui/dropdown-menu';
  import DataTableCheckbox from './data-table-checkbox.svelte';
  import { ArrowDown, ArrowUp } from 'lucide-svelte';
  import Input from '$lib/elements/ui/input/input.svelte';
  import { type Writable } from 'svelte/store';
  import { createEventDispatcher } from 'svelte';
  import type { Tag } from '$lib/sdk/fetch-client';

  interface DataItem {
    id: string;

    [key: string]: string | string[] | number | Tag;
  }

  export let tableData:Writable<DataItem[]>;
  export let columnNames;
  export let keys;
  export let totalElements: Writable<number>;

  let tags: String[] = [];

  const dispatch = createEventDispatcher();

  const table = createTable(tableData, {
    page: addPagination({ serverSide: true, serverItemCount: totalElements, initialPageSize: 10 }), //TODO: change page size, 10 only for testing
    sort: addSortBy({ serverSide: true }),
    filter: addTableFilter({ serverSide: true }),
    hide: addHiddenColumns(),
    select: addSelectedRows(),
  });

  let columns = table.createColumns([
    table.column({
      //first column only contains the checkboxes.
      accessor: (item) => {
        return item[keys[0]];
      },
      id: 'id',
      header: (_, { pluginStates }) => {
        const { allPageRowsSelected } = pluginStates.select;
        return createRender(DataTableCheckbox, {
          checked: allPageRowsSelected,
          white: true,
        });
      },
      cell: ({ row }, { pluginStates }) => {
        const { getRowState } = pluginStates.select;
        const { isSelected } = getRowState(row);

        return createRender(DataTableCheckbox, {
          checked: isSelected,
          white: false,
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
    columns = columns.concat(
      table.createColumns([
        table.column({
          accessor: (item:DataItem) => {
            return item[keys[i + 1]];
          },
          header: columnName,
          id: keys[i + 1],
        }),
      ]),
    );
  }

  columns = columns.concat(
    table.createColumns([
      table.column({
        accessor: (item) => {
          return item[keys[0]];
        },
        header: '',
        id: 'actions',
        cell: ({ value }) => {
          return createRender(DataTableActions, { id: value.toString() });
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
  const { sortKeys } = pluginStates.sort;

  const ids = flatColumns.map((col) => col.id);
  let hideForId = Object.fromEntries(ids.map((id) => [id, true]));

  $: $hiddenColumnIds = Object.entries(hideForId)
    .filter(([, hide]) => !hide)
    .map(([id]) => id);

  const hidableCols = keys.slice(1);

  function getData() {
    let sortKey: SortKey = $sortKeys[0];
    let sortString;
    if (sortKey) {
      sortString = sortKey.id + ',' + sortKey.order;
    } else sortString = '';
    dispatch('pageLoad', {
      pageIndex: $pageIndex,
      sort: sortString,
      filter: $filterValue,
    });
  }

  $: {
    $filterValue;
    getData();
  }
</script>

<div>
  <div class="flex items-center py-4">
    <Input
      bind:value={$filterValue}
      class="max-w-sm rounded-none border-0 border-b-4 border-foreground focus-visible:ring-0 focus-visible:border-b-4"
      placeholder="Suche..."
      type="text"
    />
    <DropdownMenu.Root>
      <DropdownMenu.Trigger asChild let:builder>
        <Button builders={[builder]} class="ml-auto" variant="secondary">
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
  <div>
    <Table.Root {...$tableAttrs}>
      <Table.Header class="bg-foreground">
        {#each $headerRows as headerRow}
          <Subscribe rowAttrs={headerRow.attrs()}>
            <Table.Row>
              {#each headerRow.cells as cell (cell.id)}
                <Subscribe attrs={cell.attrs()} let:attrs props={cell.props()} let:props>
                  <Table.Head {...attrs} class="[&:has([role=checkbox])]:pl-4">
                    {#if cell.id !== 'actions' && cell.id !== 'id'}
                      <Button
                        variant="ghost"
                        on:click={(event) => {
                          props.sort.toggle(event);
                          getData();
                        }}
                        class="text-white"
                      >
                        <Render of={cell.render()} />
                        <div class="flex ml-2">
                          <ArrowUp class="h-4 w-4 {props.sort.order === 'asc' ? 'text-accent' : ''}" />
                          <ArrowDown class="ml-[-4px] h-4 w-4 {props.sort.order === 'desc' ? 'text-accent' : ''}" />
                        </div>
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
    <Button
      disabled={!$hasPreviousPage}
      on:click={() => {
        $pageIndex = $pageIndex - 1;
        getData();
      }}
      size="sm"
      variant="secondary"
      >Zurück
    </Button>
    <Button
      disabled={!$hasNextPage}
      on:click={() => {
        $pageIndex = $pageIndex + 1;
        getData();
      }}
      size="sm"
      variant="secondary"
      >Weiter
    </Button>
  </div>
</div>
