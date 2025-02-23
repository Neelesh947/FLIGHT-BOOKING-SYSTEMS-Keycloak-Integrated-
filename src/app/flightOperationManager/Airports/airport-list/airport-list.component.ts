import { SelectionModel } from '@angular/cdk/collections';
import { Component, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { AirpottService } from '../../../services/airpott.service';

export interface Airport {
  airportName: string;
  isEnabled: boolean;
  location: string;
  airportCode: string;
  id:string;
}

@Component({
  selector: 'app-airport-list',
  templateUrl: './airport-list.component.html',
  styleUrl: './airport-list.component.css'
})
export class AirportListComponent implements OnInit{

  selectedTab = 0;
  displayedColumns: string[] = ['select', 'Airport Name', 'Location', 'Airport Code', 'Status'];
  dataSource: MatTableDataSource<Airport> = new MatTableDataSource<Airport>();
  airports: Airport[] = [];
  selection: SelectionModel<Airport> = new SelectionModel<Airport>(false, []);
  totalAirportCount: number = 0;
  pageSize: number = 10;  // Default page size
  isHidden: boolean | undefined;

  constructor(private airportService: AirpottService) {}

  ngOnInit(): void {
    this.getListOfAirport();
  }

  getListOfAirport(pageIndex: number = 0, pageSize: number = 10) {
      this.airportService.getListOFAirportByFOM(pageIndex, pageSize, this.isHidden).subscribe(
        (data:any) =>{
          this.airports = data.content;
          this.totalAirportCount = data.totalElements;
          this.dataSource.data = this.airports;
        }
      )
  }

  onTabChange(index: number): void{}

  onSelect(event: any, air: Airport): void {
    if (event.checked) {
      this.selection.select(air);
    } else {
      this.selection.deselect(air);
    }
  }

  // Select all rows
  selectAll(event: any): void {
    if (event.checked) {
      this.selection.select(...this.dataSource.data);
    } else {
      this.selection.clear();
    }
  }

  // Check if all rows are selected
  isAllSelected(): boolean {
    return this.selection.selected.length === this.dataSource.data.length;
  }

  // Check if the selection is indeterminate
  isIndeterminate(): boolean {
    const numSelected = this.selection.selected.length;
    return numSelected > 0 && numSelected < this.dataSource.data.length;
  }

  activateAirport(airportId:String){}

  deactivateAirport(airportId:String){}

  // Handle page change event
  pageChanged(event: any): void {
  }
}
